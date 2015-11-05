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

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class FileLogSourceTest
{
    {

        describe("File Log Source", it -> {

            it.should("Be able to read from updating files", expect -> {
                // Given
                File file = File.createTempFile("foo", ".bin");
                file.deleteOnExit();

                // when
                try (FileOutputStream stream = new FileOutputStream(file))
                {
                    stream.write(1);
                    stream.flush();
                    FileLogSource source = new FileLogSource(file);

                    // then
                    ByteBuffer buffer = source.read();
                    expect.that(buffer.limit()).is(1)
                        .and(buffer.get()).is((byte) 1);

                    // when
                    stream.write(2);
                    stream.flush();

                    // then
                    buffer = source.read();
                    expect.that(buffer.limit()).isGreaterThanOrEqualTo(1)
                        .and(buffer.get()).is((byte) 2);
                }
            });
        });

    }}
