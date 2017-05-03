/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.sources;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.insightfullogic.honest_profiler.core.sources.CantReadFromSourceException;
import com.insightfullogic.honest_profiler.core.sources.LogSource;

/**
 * LogSource implementation which maintains a fixed-size memory-mapped window ({@value #BUFFER_SIZE} bytes) on the file.
 * The read() method checks whether a certain amount of the current buffer ({@value #ELASTICITY} bytes) has been read
 * already, and if so, remaps the buffer to the new position.
 * <p>
 * The ELASTICITY is provided for the benefit of the LogParser + Conductor logic. If a partial record is read, the
 * Conductor will sleep for a bit. So ideally, BUFFER_SIZE - ELASTICITY should be large enough so that an "entire
 * record" (i.e. an entire stack) which might start at the last byte within the ELASTICITY portion still would fit into
 * the remains of the buffer.
 */
public class FileLogSource implements LogSource
{
    // Class Properties

    // Fixed buffer size
    private static final int BUFFER_SIZE = 1024 * 1024 * 100; // 100 MB
    // Remap if more than ELASTICITY has been read from the current buffer
    private static final int ELASTICITY = 1024 * 1024 * 1; // 1 MB

    // Instance Properties

    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final File file;

    private MappedByteBuffer buffer;
    // the offset in the file where the current buffer starts.
    private long currentOffset = 0;

    private int previousPosition;

    // Instance Constructors

    public FileLogSource(final File file)
    {
        this.file = file;
        try
        {
            raf = new RandomAccessFile(file, "r");
            channel = raf.getChannel();
            mapBuffer(0);
        }
        catch (IOException e)
        {
            throw new CantReadFromSourceException(e);
        }
    }

    // Instance Accessors

    public File getFile()
    {
        return file;
    }

    // LogSource Implementation

    @Override
    public ByteBuffer read()
    {
        try
        {
            int position = buffer.position();
            boolean hasRemaining = buffer.hasRemaining();

            if (position == previousPosition)
            {
                // The buffer was rewound after the previous read. Either the data was not written (0 was read) or a
                // buffer underflow occurred. Don't update currentOffset, or we'll skip data.
                mapBuffer(currentOffset);
            }
            // channel.size() is *very* expensive so we try and minimize its invocation.
            else if (position > ELASTICITY || (!hasRemaining && currentOffset < channel.size()))
            {
                // If the buffer is empty but the file size increased, or we've read more than ELASTICITY bytes, the
                // currentOffset is updated and the buffer is remapped.
                currentOffset += position;
                mapBuffer(currentOffset);
            }
            else
            {
                previousPosition = position;
            }
        }
        catch (IOException e)
        {
            throw new CantReadFromSourceException(e);
        }

        return buffer;
    }

    @Override
    public void close() throws IOException
    {
        buffer = null;
        raf.close();
    }

    // Shame there's no simple abstraction for reading over both files and
    // network bytebuffers

    /**
     * Replaces the current buffer by a new ByteBuffer which is memory-mapped onto a BUFFER_SIZE (10 MB at time of
     * writing) window starting at the specified offset.
     * <p>
     * @param offset the offset in the file of the area which will be mapped into the buffer
     * @throws IOException any I/O exceptions encountered trying to map a portion of the file into memory 
     */
    private void mapBuffer(long offset) throws IOException
    {
        int length = BUFFER_SIZE;
        long fileEnd = channel.size();

        if (offset + BUFFER_SIZE > fileEnd)
        {
            // Cast to int is safe, since the test determines that the int
            // BUFFER_SIZE > fileEnd - offset)
            length = (int) (fileEnd - offset);
        }
        buffer = channel.map(READ_ONLY, offset, length);

        // Ensures we know next time read() is called we can easily test whether the position moved or was remapped in a
        // single comparison.
        previousPosition = -1;
    }

    @Override
    public String toString()
    {
        return "FileLogSource{" + "file=" + file + '}';
    }
}
