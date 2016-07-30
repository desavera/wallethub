/*
 * WalletHub Trial
 *
 * this function capializes the first char of each word from input
 *
 * reference : http://joezack.com/2008/10/20/mysql-capitalize-function/ 
 *
 * desavera@gmail.com
 */

CREATE FUNCTION CAP_FIRST (input VARCHAR(255))

RETURNS VARCHAR(255)

DETERMINISTIC

BEGIN
	DECLARE len INT;
	DECLARE i INT;

	SET len   = CHAR_LENGTH(input);
	SET input = LOWER(input);
	SET i = 0;

	WHILE (i < len) DO
		IF (MID(input,i,1) = ' ' OR i = 0) THEN
			IF (i < len) THEN
				SET input = CONCAT(
					LEFT(input,i),
					UPPER(MID(input,i + 1,1)),
					RIGHT(input,len - i - 1)
				);
			END IF;
		END IF;
		SET i = i + 1;
	END WHILE;

	RETURN input;
END;

