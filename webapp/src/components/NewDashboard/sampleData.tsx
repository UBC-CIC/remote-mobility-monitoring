function sampleData(){
    const patients = ["Emily Smith"];
    const metrics = [
        "stepLength",
        "doubleSupportTime",
        "walkingSpeed",
        "walkingAsymmetry",
        "distanceWalked",
        "stepCount"
    ];
    
    const data = [];
    
    for (let i = 0; i < patients.length; i++) {
        const patientName = patients[i];
    
        for (let j = 0; j < metrics.length; j++) {
            const metricName = metrics[j];
    
            for (let k = 0; k < 10; k++) {
                const date = new Date(2023, 3, k + 1).toISOString();
    
                const metric = {
                    patient_name: patientName,
                    metric_name: metricName,
                    metric_value: Math.floor(Math.random() * 100 + 1).toFixed(1),
                    timestamp: date,
                };
    
                data.push(metric);
            }
        }
    }
    
    return data;
    

}
export default sampleData;
