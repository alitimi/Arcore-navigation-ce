package com.example.arcore_navigation_ce;

public class SensorFilter {

    private SensorFilter() {
    }

    static float sum(float[] array) {
        float retrieveValues = 0;
        for (float v : array) {
            retrieveValues += v;
        }
        return retrieveValues;
    }

    public static float[] cross(float[] arrayA, float[] arrayB) {
        float[] retArray = new float[3];
        retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
        retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
        retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
        return retArray;
    }

    static float norm(float[] array) {
        float retrieveValues = 0;
        for (float v : array) {
            retrieveValues += v * v;
        }
        return (float) Math.sqrt(retrieveValues);
    }


    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float[] normalize(float[] a) {
        float[] retrieveValues = new float[a.length];
        float norm = norm(a);
        for (int i = 0; i < a.length; i++) {
            retrieveValues[i] = a[i] / norm;
        }
        return retrieveValues;
    }

}
