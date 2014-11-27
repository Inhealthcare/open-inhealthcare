-- Table: "SOAPAudit"

DROP TABLE "SOAPAudit";

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SOAPAudit"
  OWNER TO postgres;
