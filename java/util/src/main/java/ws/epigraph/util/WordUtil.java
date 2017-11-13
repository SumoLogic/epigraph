/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class WordUtil {

  private WordUtil() {}

  public static String suggest(String mistype, Collection<String> options, String format, String noSuggestion) {
    final double threshold = 0.5; // don't suggest anything below this

    Map<String, Double> toDistance =
        options.stream().collect(Collectors.toMap(Function.identity(), s -> similarity(mistype, s)));

    return toDistance.entrySet().stream()
        .filter(e -> e.getValue() >= threshold)
        .max(Map.Entry.comparingByValue())
        .map(e -> String.format(format, e.getValue()))
        .orElse(noSuggestion);
  }

  /**
   * Calculates the similarity (a number within 0 and 1) between two strings.
   * https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
   */
  public static double similarity(String s1, String s2) {
    String longer = s1, shorter = s2;
    if (s1.length() < s2.length()) { // longer should always have greater length
      longer = s2;
      shorter = s1;
    }
    int longerLength = longer.length();
    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
  }

  // Example implementation of the Levenshtein Edit Distance
  // See http://rosettacode.org/wiki/Levenshtein_distance#Java
  public static int editDistance(String s1, String s2) {
    s1 = s1.toLowerCase();
    s2 = s2.toLowerCase();

    int[] costs = new int[s2.length() + 1];
    for (int i = 0; i <= s1.length(); i++) {
      int lastValue = i;
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0)
          costs[j] = j;
        else {
          if (j > 0) {
            int newValue = costs[j - 1];
            if (s1.charAt(i - 1) != s2.charAt(j - 1))
              newValue = Math.min(
                  Math.min(newValue, lastValue),
                  costs[j]
              ) + 1;
            costs[j - 1] = lastValue;
            lastValue = newValue;
          }
        }
      }
      if (i > 0)
        costs[s2.length()] = lastValue;
    }
    return costs[s2.length()];
  }
}
