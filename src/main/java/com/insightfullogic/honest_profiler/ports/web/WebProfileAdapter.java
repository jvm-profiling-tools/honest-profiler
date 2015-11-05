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
package com.insightfullogic.honest_profiler.ports.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.slf4j.Logger;

public class WebProfileAdapter implements ProfileListener
{

    private final ObjectMapper mapper = new ObjectMapper();

    private final Logger logger;
    private final VirtualMachine machine;
    private final ClientConnections clients;

    public WebProfileAdapter(final Logger logger, final VirtualMachine machine, final ClientConnections clients)
    {
        this.logger = logger;
        this.machine = machine;
        this.clients = clients;
    }

    @Override
    public void accept(Profile profile)
    {
        try
        {
            NewProfileMessage newProfile = new NewProfileMessage(machine.getId(), profile);
            String message = mapper.writeValueAsString(newProfile);
            clients.sendAll(message);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

}
