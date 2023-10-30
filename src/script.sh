echo 0 > ./cost.txt
for file in ./tests/*.txt; do
    java ./VehicleRoutingProblem.java "$file" >> ./cost.txt
done
echo test 10 - half-size extra check >> ./result.txt
java ./Script.java ./cost.txt >> ./result.txt