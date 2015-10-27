Purpose:
=======
Yarn logs are stored in TFile format. Currently it is possible to use TFileLoader (in tez-tools) to parse it and get the container, machine on which this container was run etc for parsing.

However, when app logs are downloaded for offline analysis, they are stored in text file.  This is not understandable by TFileLoader and it ends up messing up with the machine details.  This tool (ideally could have been awk or perl or python), looks for the container details and adds "machine" details to every line in that container. So it becomes  a lot more easier for further analysis (e.g shuffle performance analysis scripts).

Not sure if anyone would be interested in adding "container" information as well to every line. But we can look at it when it comes up.

Build and Run:
==============
1. mvn clean package
2. java -jar ./target/yarn-log-machine-appender-1.0-SNAPSHOT.jar <YARN_APPLICATION_LOG_FILE_LOCATION_IN_LOCAL_SYSTEM>
