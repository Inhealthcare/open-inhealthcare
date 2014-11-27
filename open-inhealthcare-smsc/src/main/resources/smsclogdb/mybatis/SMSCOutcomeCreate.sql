-- Table: "SMSCOutcome"

--DROP TABLE "SMSCOutcome";

CREATE TABLE "SMSCOutcome"
(
  id serial NOT NULL,
  "timestamp" character(29),
  "conversationId" character(36),
  "outcome" character varying(200),

  CONSTRAINT "SMSCOutcome_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SMSCOutcome"
  OWNER TO postgres;
