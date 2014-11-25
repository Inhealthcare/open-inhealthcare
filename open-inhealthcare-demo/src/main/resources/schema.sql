CREATE TABLE "ITKAudit"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "type" character(12),
  "conversationId" character(36),
  "trackingId" character(36),
  "payloadId" character(41),
  "serviceId" character varying(100),
  "profileId" character varying(100),
  "senderAddress" character varying(100),
  "nhsNumber" character(10),
  "localPatientId" character varying(50),
  "localAuditId" character varying(100),
  "spineRoleProfileId" character varying(50),
  "spineRoleId" character varying(50),
  "spineUserId" character varying(50),
  CONSTRAINT "ITKAudit_pkey" PRIMARY KEY (id)
);

CREATE TABLE "SOAPAudit"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "type" character(12),
  "status" character(10),
  "conversationId" character(36),
  "messageId" character(36),
  "creationTime" character(30),
  "to" character varying(100),
  "action" character varying(100),
  "userId" character varying(100),
  CONSTRAINT "SOAPAudit_pkey" PRIMARY KEY (id)
);

CREATE TABLE "SMSCInput"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "request" character varying(4096),

  CONSTRAINT "SMSCInput_pkey" PRIMARY KEY (id)
);

CREATE TABLE "SMSCOutcome"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "outcome" character varying(200),

  CONSTRAINT "SMSCOutcome_pkey" PRIMARY KEY (id)
);

CREATE TABLE "SMSPRequest"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "request" character varying(4096),

  CONSTRAINT "SMSPRequest_pkey" PRIMARY KEY (id)
);

CREATE TABLE "SMSPResponse"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "responseCode" character(10),
  "response" character varying(4096),

  CONSTRAINT "SMSPResponse_pkey" PRIMARY KEY (id)
);