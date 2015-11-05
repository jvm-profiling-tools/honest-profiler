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
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;

import java.io.PrintStream;

import static com.insightfullogic.honest_profiler.ports.console.ProfileFormat.ALL;

public class ProfileView implements ProfileListener
{

    private final Console output;

    private ProfileFormat profileFormat = ALL;

    public ProfileView(Console output)
    {
        this.output = output;
    }

    @Override
    public void accept(Profile profile)
    {
        PrintStream out = output.stream();
        printHeader(profile, out);
        out.println();
        profileFormat.printProfile(profile, out);
        out.println();
    }

    private void printHeader(Profile profile, PrintStream out)
    {
        out.print("Number of stack traces: ");
        out.print(Integer.toString(profile.getTraceCount()));
    }

    public void setProfileFormat(ProfileFormat profileFormat)
    {
        this.profileFormat = profileFormat;
    }

}
