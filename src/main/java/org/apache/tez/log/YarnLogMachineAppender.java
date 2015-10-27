/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tez.log;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * When we get entire application log, it would be in text format and pig
 * script might not be able to make use of TFileLoader.
 *
 * Pig script needs to understand the machine, key, line format.
 * So we add machine detail to every line.
 * <p/>
 * e.g Container: container_e22_1444351936981_38910_01_001166 on hads0102.homedepot.com_45454
 * Parse machine name and add to all lines until we hit next Container.
 *
 * This is a dirty hack. But who cares!!!.
 */
public class YarnLogMachineAppender {

  static final Pattern CONTAINER_PATTERN =
      Pattern.compile("^Container:.* on (.*)");

  static final String CONTAINER = "Container";

  static final String TAB = "\t";

  public static void main(String[] args) throws IOException {
    //Junk checks
    if (args == null || args.length != 1) {
      throw new IllegalArgumentException("Please provide a input filename");
    }

    File inputFile = new File(args[0]);
    if (!inputFile.exists()) {
      throw new FileNotFoundException("Input file " + inputFile + " does not "
          + "exist");
    }

    //Read the file and write the output
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    File output = new File(args[0] + ".fixed");
    BufferedWriter writer = new BufferedWriter(new FileWriter(output));
    String machine = null;
    while (reader.ready()) {
      String line = reader.readLine().trim();
      if (line.length() == 0) {
        continue;
      }

      if (line.startsWith(CONTAINER)) {
        System.out.println(line);
        Matcher matcher = CONTAINER_PATTERN.matcher(line);
        if (matcher.find()) {
          machine = matcher.group(1);
          continue;
        }
      }

      //Write "machineName '\t' line"
      if (machine != null) {
        line = machine + TAB + line;
        writer.write(line);
        writer.newLine();
      }
    }
    reader.close();
    writer.close();
    System.out.println("Done!!! .Output is in  " + output);
  }
}
