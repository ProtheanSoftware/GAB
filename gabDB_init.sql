USE gab;

DROP TABLE IF EXISTS t_messages, t_likes, t_users;

CREATE TABLE t_users (rowId BIGINT(64) UNSIGNED, fb_id VARCHAR(30), name VARCHAR(255)
, PRIMARY KEY (rowId)

);


CREATE TABLE t_likes (rowId BIGINT(64) UNSIGNED, originId BIGINT(64) UNSIGNED, likedId BIGINT(64) UNSIGNED
, PRIMARY KEY (rowId)
, INDEX (originId)
, INDEX (likedId)
, FOREIGN KEY (originId)
      REFERENCES t_users(rowId)
, FOREIGN KEY (likedId)
      REFERENCES t_users(rowId)
);


CREATE TABLE t_messages (rowId BIGINT(64) UNSIGNED, senderId BIGINT(64) UNSIGNED, recieverId BIGINT(64) UNSIGNED, curTimeStamp TIMESTAMP, payload VARCHAR(255)
, PRIMARY KEY (rowId)
, INDEX (senderId)
, INDEX (recieverId)
, FOREIGN KEY (senderId)
      REFERENCES t_users(rowId)
	ON UPDATE CASCADE ON DELETE RESTRICT
, FOREIGN KEY (recieverId)
      REFERENCES t_users(rowId)
	ON UPDATE CASCADE ON DELETE RESTRICT
);


SET @KalleId =24214123940151296, @LarsId=24214123940151297, @JasmineId:=24214123940151298;


INSERT INTO t_users VALUES (@KalleId, 'kalle.Stropp', 'Kalle');
INSERT INTO t_users VALUES (@LarsId, '192003221', 'Lars');
INSERT INTO t_users VALUES (@JasmineId, 'SexyLady95', 'Jasmine');

INSERT INTO t_likes  VALUES (UUID_SHORT(), @KalleId, @LarsId);
INSERT INTO t_likes  VALUES (UUID_SHORT(), @KalleId, @JasmineId);
INSERT INTO t_likes  VALUES (UUID_SHORT(), @JasmineId, @KalleId);
INSERT INTO t_likes  VALUES (UUID_SHORT(), @LarsId, @KalleId);
INSERT INTO t_likes  VALUES (UUID_SHORT(), @LarsId, @JasmineId);
