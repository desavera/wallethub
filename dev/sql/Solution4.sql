/*
 * WalletHub Trial
 *
 * this query checks BUGs open in a specific date range. On any given day 
 * a bug is open if the open_date is on or before that day and close_date 
 * is after that day.
 *
 * desavera@gmail.com
 */


CREATE FUNCTION CAP_FIRST (idate DATE,fdate DATE)

RETURNS INT

DETERMINISTIC

BEGIN

	SET count   = 0;
	WHILE idate < fdate DO 

		SET count = count + SELECT COUNT(ID) FROM BUGS WHERE OPEN_DATE <= idate AND fdate > idate;
		SET idate = idate + interval 1 day;


	END WHILE;

	RETURN count;
END;


