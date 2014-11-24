-- Table: "SMSPRequest"

DROP TABLE "SMSPRequest";

CREATE TABLE "SMSPRequest"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "request" character varying(4096),

  CONSTRAINT "SMSPRequest_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SMSPRequest"
  OWNER TO postgres;
