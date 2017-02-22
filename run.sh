ant clean
ant compile
ant jar
echo "Running sem10scorer 10 times on the same gold and predicted files"
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
java -jar sem10scorer.jar gold=gold.txt annotation=predicted.txt task=NoInstantiation 2>&1 | tail -n 8 | head -n 1
