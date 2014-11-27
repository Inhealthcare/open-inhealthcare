-- Table: "SMSPResponse"

DROP TABLE "SMSPResponse";

CREATE TABLE "SMSPResponse"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "responseCode" character(10),
  "response" character varying(4096),

  CONSTRAINT "SMSPResponse_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SMSPResponse"
  OWNER TO postgres;
