-- Table: "ITKAudit"

DROP TABLE "ITKAudit";

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "ITKAudit"
  OWNER TO postgres;
