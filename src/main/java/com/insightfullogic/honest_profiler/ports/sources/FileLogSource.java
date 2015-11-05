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

import com.insightfullogic.honest_profiler.core.sources.CantReadFromSourceException;
import com.insightfullogic.honest_profiler.core.sources.LogSource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

public class FileLogSource implements LogSource
{

    private final FileChannel channel;
    private final File file;

    private MappedByteBuffer buffer;

    public FileLogSource(final File file)
    {
        this.file = file;
        try
        {
            channel = new RandomAccessFile(file, "r").getChannel();
            remapFile(channel.size());
        }
        catch (IOException e)
        {
            throw new CantReadFromSourceException(e);
        }
    }

    // Shame there's no simple abstraction for reading over both files
    // and network bytebuffers
    private void remapFile(final long size) throws IOException
    {
        buffer = channel.map(READ_ONLY, 0, size);

    }

    @Override
    public ByteBuffer read()
    {
        try
        {
            int limit = buffer.limit();
            long channelSize = channel.size();
            if (channelSize > limit)
            {
                int oldPosition = buffer.position();
                remapFile(channelSize);
                buffer.position(oldPosition);
            }
        }
        catch (IOException e)
        {
            throw new CantReadFromSourceException(e);
        }

        return buffer;
    }

    public File getFile()
    {
        return file;
    }

    @Override
    public void close() throws IOException
    {
        channel.close();
    }

    @Override
    public String toString()
    {
        return "FileLogSource{" +
            "file=" + file +
            '}';
    }
}
