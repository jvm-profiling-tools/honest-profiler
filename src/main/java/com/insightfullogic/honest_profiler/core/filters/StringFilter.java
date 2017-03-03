/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.insightfullogic.honest_profiler.core.collector.Frame;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

public class StringFilter implements Filter
{
    private final Map<Long, Boolean> methods;
    private final String input;

    private Mode mode;
    private Function<Frame, String> stringProvider;
    private Predicate<String> filterMethod;

    public StringFilter(Mode mode, Function<Frame, String> stringProvider, String input)
    {
        methods = new HashMap<>();
        this.input = input;
        this.mode = mode;
        this.stringProvider = stringProvider;
        generateFilterMethod();
    }

    private void generateFilterMethod()
    {
        switch (mode)
        {
            case CONTAINS:
                filterMethod = string -> string.contains(input);
                break;
            case EQUALS:
                filterMethod = string -> string.equals(input);
                break;
            case ENDS_WITH:
                filterMethod = string -> string.endsWith(input);
                break;
            case STARTS_WITH:
                filterMethod = string -> string.startsWith(input);
                break;
            case MATCHES:
                Pattern pattern = Pattern.compile(input);
                filterMethod = string -> pattern.matcher(string).matches();
            default:
                throw new RuntimeException(
                    "Filter Mode " + mode + " not supported in ClassNameFilter.");
        }
    }

    @Override
    public void filter(Profile profile)
    {
        filterFlatProfile(profile);
        filterTreeProfile(profile);
    }

    private void filterFlatProfile(Profile profile)
    {
        profile.getFlatByMethodProfile().removeIf(entry -> !classNameMatches(entry.getFrameInfo()));
    }

    private void filterTreeProfile(Profile profile)
    {
        profile.getTrees().removeIf(tree -> filterNode(tree.getRootNode()));
    }

    private boolean filterNode(ProfileNode node)
    {

        if (classNameMatches(node.getFrameInfo()))
        {
            return false;
        }

        node.getChildren().removeIf(this::filterNode);
        return node.getChildren().isEmpty();
    }

    private boolean classNameMatches(Frame sampleFrameInfo)
    {
        return methods.computeIfAbsent(
            sampleFrameInfo.getMethodId(),
            id -> filterMethod.test(stringProvider.apply(sampleFrameInfo)));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        StringFilter that = (StringFilter) o;
        return (mode == that.mode)
            && (input == null ? that.input == null : input.equals(that.input));
    }

    @Override
    public int hashCode()
    {
        return (37 * mode.ordinal()) + (input != null ? input.hashCode() : 0);
    }
}
