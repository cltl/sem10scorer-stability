This repo demonstrates that the result of `sem10scorer.v2.jar` depends on 
the order that the script consumes annotations. 

In the original implementation,
coreference annotations are stored in a HashSet therefore the order is 
arbitrary (see `CoreferenceChainsExtractor.java`, line 111 in the original code,
line 125 in the new code). This means the scorer changes its mind when run on
different computers which I actually observed on my Mac and a Linux virtual
machine.
 
To demonstrate the effect, I ran two experiments:

When I fix the order of single coreference annotations (comment out line 123), 
the result is stable.

```
$ ./run.sh
[cleaning, building, packaging...]
Running sem10scorer 10 times on the same gold and predicted files
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
True Positives: 10
``` 

When I shuffle it (uncomment line 123), the result switches back and forth 
between 9 and 10 true positives.
```
$ ./run.sh
[cleaning, building, packaging...]
Running sem10scorer 10 times on the same gold and predicted files
True Positives: 9
True Positives: 10
True Positives: 10
True Positives: 9
True Positives: 9
True Positives: 9
True Positives: 9
True Positives: 10
True Positives: 10
True Positives: 9
```