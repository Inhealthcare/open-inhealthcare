# open-inhealthcare

Inhealthcare Open Integration library with open SMSC (Spine Mini Service Client)

## Build JSAT application

	cd open-inhealthcare
	mvn install

## Run JSAT application

	cd open-inhealthcare-core
	mvn exec:java -Dexec.mainClass="uk.co.inhealthcare.open.jsat.JavaSpineAutoTrace"

## Simulate SMSP using HSCIC ITK Toolkit Workbench (ITK TKW)

* Download the installer from http://systems.hscic.gov.uk/sa/tools
* Run TKWAutotestManager
* Run the simulator listening for PDS Mini Services messages

## Send HL7 Message to JSAT endpoint using HAPI TestPanel

* Download TestPanel from http://hl7api.sourceforge.net/hapi-testpanel/
* Run the java application
* Create a sending connection on localhost:8889
* Create a receiving connection on localhost:8880
* Send a sample message to the JSAT message

### Sample messages

#### AD5^A28

	MSH|^~\&|||||20140529080238.241+0100||ADT^A28^ADT_A05|201|T|2.4
	EVN|A28|201405281200
	PID|||8000002^^^RMW00^MR~9990500002^^^NHS^NH||SMSPDEMO23^PATIENT^^^Mrs||19581119|F|||1 MY AVENUE^^WAKEFIELD^WEST YORKS^WF1 1XX|||||||||||||||||||||NSTS02|||
	PD1|||SAUGHALL PRACTICE^^81123|G9991234^Mister GP^GP^^^Dr|||||

#### AD5^A31

	MSH|^~\&|||||20140529080238.241+0100||ADT^A31^ADT_A05|201|T|2.4
	EVN|A31|201405281200
	PID|||8000002^^^RMW00^MR||SMSPDEMO30^PATIENT^^^Mrs||19581119|F|||1 MY AVENUE^^WAKEFIELD^WEST YORKS^WF1 1XX|||||||||||||||||||||NSTS02|||
	PD1|||SAUGHALL PRACTICE^^81123|G9991234^Mister GP^GP^^^Dr|||||
