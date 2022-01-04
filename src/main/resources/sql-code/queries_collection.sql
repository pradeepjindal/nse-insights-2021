(
SELECT date_trunc('day', dd):: date
FROM generate_series( '2007-02-01'::timestamp, '2007-03-01'::timestamp, '1 day'::interval) dd,

SELECT date_trunc('day', dd):: date
FROM generate_series( (select max(trade_Date) from nse_cash_market_tab where tdn<20211210)
					 , (select max(trade_Date) from nse_cash_market_tab), '1 day'::interval) dd

--------
select *
from
(
	select distinct file_date, trade_Date
	, case when file_date = trade_date then 'match' else '' end as sync
	from nse_lot_size_tab
) abc
where sync = 'match'
order by trade_Date
--------
