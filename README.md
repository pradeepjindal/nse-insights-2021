# nse-insights-2021
nse data processing and insights version 2021


ToDo

generate a FILTERED delivery spike report separately
generate a FILTERED oi spike report separately

add VDR

add open = low
add open = high
add close = open

atpRsiChange column needs to be added, to find divergence


MFI
MFI generally favors the bulls when the indicator is above 50 and the bears when below 50.
If the underlying price makes a new high or low that isn't confirmed by the MFI (20, 80), this divergence can signal a price reversal.

add adx
add macd
add rsi change
add mfi change

50pn
del fall by 10%
atp is down
rsi is weak
stock is in falling trend in chart

50pp
pick only positive rsi chg by at least 1
pick only above rsi 60
filter only postitve mfi and positive chg by at least 1
consult the chart

find when mfi rise more then rsi, means scrip is going to rise

find scrip which drops for 50p daily or rise by 50p daily

3day continue fall report, 3day continue rise report

divergence report - price rising and rsi falling (3d)

find what happens to tdy topper on next day
find what happens to tdy open=low on next day

detect bullsih and bearish engulf

sbi
buy 2 rs down from open and 5 rs down from open
sell 3 rs high from open


Tech To Do
make calc csv and db reconcile routine


TECH DEBT
separate DeliverySpikeDto into ppfDto
file validator - csv files and db dates
data validator - csv rows and db rows



DESIGN
favor composition over inheritance

Scala has support for Mixins as well. In Scala those are called Traits. 
Traits are Mixins just with some slightly different properties from a programming language designers point of view 
like Mixins require linearization while Traits are flattened and Traits traditionally don’t contain states. 
But that shouldn’t worry you too much. For the sake of simplicity we can say Mixins and Traits are the same.
Ruby's Mixins and Scala's Traits support state and behavior, while Java and Kotlin's interfaces only support behavior.
