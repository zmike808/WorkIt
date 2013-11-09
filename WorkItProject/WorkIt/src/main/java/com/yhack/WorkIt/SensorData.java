package com.yhack.WorkIt;

/**
 * Created by Robert Rouhani on 11/9/13.
 */
public class SensorData
{
    private float ax, ay, az;
    private float gx, gy, gz;

    public SensorData(String data)
    {
        String[] ag = data.split("G");
        if (ag.length < 2)
            return;

        String a = ag[0], g = ag[1];

        String[] av = a.split(",");
        String[] gv = g.split(",");
        if (av.length < 3 || gv.length < 3)
            return;

        ax = Float.parseFloat(av[0]);
        ay = Float.parseFloat(av[1]);
        az = Float.parseFloat(av[2]);

        gx = Float.parseFloat(gv[0]);
        gy = Float.parseFloat(gv[1]);
        gz = Float.parseFloat(gv[2]);
    }

    public float getAx()
    {
        return ax;
    }

    public float getAy()
    {
        return ay;
    }

    public float getAz()
    {
        return az;
    }

    public float getGx()
    {
        return gx;
    }

    public float getGy()
    {
        return gy;
    }

    public float getGz()
    {
        return gz;
    }

    @Override
    public String toString()
    {
        return "Acceleration: { " + ax + ", " + ay + ", " + az + " }, Rotation: { " + gx + ", " + gy + ", " + gz + " }";
    }
}
