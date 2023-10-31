> ./cost.txt
for file in ./tests/*.txt; do
    java ./VehicleRoutingProblem.java "$file" >> ./cost.txt
done
echo corrected - half size extra check >> ./result.txt
java ./Script.java ./cost.txt >> ./result.txt
cat ./result.txt