package com.google.search.robotstxt.spec;

import com.google.search.robotstxt.spec.specification.SpecificationProtos;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Handles a parser that outputs its outcome by exiting with a specific code */
public class ExitcodeParserMatcher implements ParserMatcher {
  @Override
  public SpecificationProtos.Outcome getOutcome(
      String robotsTxtContent, String url, String userAgent, CMDArgs cmdArgs)
      throws IOException, InterruptedException {
    File dir = new File("./src/main/resources/Temp");

    File robotsTxtPath = File.createTempFile("robots_", ".tmp", dir);

    FileWriter writer = new FileWriter(robotsTxtPath);
    writer.write(robotsTxtContent);
    writer.close();

    String command =
        cmdArgs.getCommand(robotsTxtPath.getAbsolutePath(), url, "\"" + userAgent + "\"");
    Process process = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});

    process.waitFor();
    int exitCode = process.exitValue();
    if (Integer.toString(exitCode).equals(cmdArgs.getAllowedPattern())) {
      return SpecificationProtos.Outcome.ALLOWED;
    } else if (Integer.toString(exitCode).equals(cmdArgs.getDisallowedPattern())) {
      return SpecificationProtos.Outcome.DISALLOWED;
    }
    return SpecificationProtos.Outcome.UNSPECIFIED;
  }
}
