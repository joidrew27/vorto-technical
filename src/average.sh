echo 0 > ./cost.txt
for file in ./tests/*.txt; do
    java ./VehicleRoutingProblem.java "$file" >> ./cost.txt
done
java ./Script.java ./cost.txt > ./result.txt