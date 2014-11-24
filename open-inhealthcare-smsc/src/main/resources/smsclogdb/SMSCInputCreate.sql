-- Table: "SMSCInput"

--DROP TABLE "SMSCInput";

CREATE TABLE "SMSCInput"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "request" character varying(4096),

  CONSTRAINT "SMSCInput_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SMSCInput"
  OWNER TO postgres;
