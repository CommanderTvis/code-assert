/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.codeassert.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.MethodAnnotation;
import guru.nidi.codeassert.util.BaseIgnores;
import guru.nidi.codeassert.util.IgnoreSource;
import guru.nidi.codeassert.util.LocationMatcher;
import guru.nidi.codeassert.util.Reason;

/**
 *
 */
public class BugCollector implements IgnoreSource<BugCollector> {
    /**
     * @param maxRank     maximum rank for a bug to be collected.
     * @param minPriority minimum priority for a bug to be collected.
     * @return A new BugCollector with the given configuration.
     * @see edu.umd.cs.findbugs.Priorities
     */
    public static BugCollector simple(final Integer maxRank, final Integer minPriority) {
        return new BugCollector() {
            @Override
            public boolean accept(BugInstance bug) {
                return (maxRank == null || bug.getBugRank() <= maxRank) &&
                        (minPriority == null || bug.getPriority() <= minPriority);
            }
        };
    }

    public Reason<BugCollector> because(String reason) {
        return new Reason<>(this, reason);
    }

    public Ignores ignore(String... types) {
        return new Ignores(types);
    }

    public Ignores ignoreAll() {
        return new Ignores(new String[0]);
    }

    public boolean accept(BugInstance bug) {
        return true;
    }

    public class Ignores extends BaseIgnores<BugCollector> {
        protected Ignores(String[] ignores) {
            super(ignores);
        }

        public BugCollector in(final LocationMatcher matcher) {
            return new BugCollector() {
                @Override
                public boolean accept(BugInstance bug) {
                    final MethodAnnotation method = bug.getPrimaryMethod();
                    final String className = bug.getPrimaryClass().getClassName();
                    final String methodName = method == null ? null : method.getMethodName();
                    return BugCollector.this.accept(bug) && !matcher.matches(bug.getType(), className, methodName);
                }
            };
        }
    }
}