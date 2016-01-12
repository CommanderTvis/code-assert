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
package guru.nidi.codeassert.snippets;

import edu.umd.cs.findbugs.Priorities;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.findbugs.BugCollector;
import guru.nidi.codeassert.findbugs.FindBugsAnalyzer;
import guru.nidi.codeassert.findbugs.FindBugsResult;
import guru.nidi.codeassert.pmd.Ruleset;
import org.junit.Ignore;
import org.junit.Test;

import static guru.nidi.codeassert.findbugs.FindBugsMatchers.hasNoBugs;
import static org.junit.Assert.assertThat;

@Ignore
//## findBugs
public class FindBugsTest {
    @Test
    public void findBugs() {
        // Analyze all sources in src/main/java
        AnalyzerConfig config = AnalyzerConfig.maven().main();

        // Only treat bugs with rank < 17 and with NORMAL_PRIORITY or higher
        // Ignore the given bug types in the given classes / methods.
        BugCollector collector = new BugCollector().maxRank(17).minPriority(Priorities.NORMAL_PRIORITY)
                .just(In.everywhere().ignore("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"))
                .because("It's checked and OK like this",
                        In.classes(DependencyRules.class, Ruleset.class).ignore("DP_DO_INSIDE_DO_PRIVILEGED"),
                        In.locs("ClassFileParser#parse", "*Test", "Rulesets$*").ignore("URF_UNREAD_FIELD"));

        FindBugsResult result = new FindBugsAnalyzer(config, collector).analyze();
        assertThat(result, hasNoBugs());
    }
}
//##