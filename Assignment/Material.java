import gmaths.*;

public class Material {

    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
    public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.05f, 0.05f, 0.05f);
    public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);
    public static final float DEFAULT_SHININESS = 32;
    private static final int LIGHT_COUNT = 3;

    private Vec3 ambient;
    private Vec3[] diffusePoint = new Vec3[LIGHT_COUNT];
    private Vec3[] diffuseSpot = new Vec3[LIGHT_COUNT];
    private Vec3[] specularPoint = new Vec3[LIGHT_COUNT];
    private Vec3[] specularSpot = new Vec3[LIGHT_COUNT];
    private Vec3 emission;
    private float shininess;

    public Material() {
        ambient = new Vec3(DEFAULT_AMBIENT);
        for (int i = 0; i < LIGHT_COUNT; i++) {
            diffusePoint[i] = new Vec3(DEFAULT_DIFFUSE);
            diffuseSpot[i] = new Vec3(DEFAULT_DIFFUSE);
            specularPoint[i] = new Vec3(DEFAULT_SPECULAR);
            specularSpot[i] = new Vec3(DEFAULT_SPECULAR);
        }

        emission = new Vec3(DEFAULT_EMISSION);
        shininess = DEFAULT_SHININESS;
    }

    public String toString() {
        return "a: " + ambient + ", dP: " + diffusePoint +", sP: " + specularPoint + ", e:" + emission + ", shininess:" + shininess;
    }

    // ------------ Ambient ------------ \\

    public void setAmbient(float red, float green, float blue) {
        ambient.x = red;
        ambient.y = green;
        ambient.z = blue;
    }

    public void setAmbient(Vec3 rgb) {
        setAmbient(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getAmbient() {
        return new Vec3(ambient);
    }

    // ------------ Diffuse ------------ \\

    public void setDiffusePoint(int i, float red, float green, float blue) {
        diffusePoint[i].x = red;
        diffusePoint[i].y = green;
        diffusePoint[i].z = blue;
    }

    public void setDiffuseSpot(int i, float red, float green, float blue) {
        diffuseSpot[i].x = red;
        diffuseSpot[i].y = green;
        diffuseSpot[i].z = blue;
    }

    public void setAllDiffusePoints(float red, float green, float blue) {
        for (int i = 0; i < LIGHT_COUNT; i++) {
            setDiffusePoint(i, red, green, blue);
        }
    }

    public void setAllDiffuseSpots(float red, float green, float blue) {
        for (int i = 0; i < LIGHT_COUNT; i++) {
            setDiffuseSpot(i, red, green, blue);
        }
    }

    public Vec3 getDiffusePoint(int i) {
        return diffusePoint[i];
    }

    public Vec3 getDefaultDiffusePoint() {
        return DEFAULT_DIFFUSE;
    }

    public Vec3 getDiffuseSpot(int i) {
        return diffuseSpot[i];
    }

    // ------------ Specular ------------ \\

    public void setSpecularPoint(int i, float red, float green, float blue) {
        specularPoint[i].x = red;
        specularPoint[i].y = green;
        specularPoint[i].z = blue;
    }

    public void setSpecularSpot(int i, float red, float green, float blue) {
        specularSpot[i].x = red;
        specularSpot[i].y = green;
        specularSpot[i].z = blue;
    }

    public void setAllSpecularPoints(float red, float green, float blue) {
        for (int i = 0; i < LIGHT_COUNT; i++) {
            setSpecularPoint(i, red, green, blue);
        }
    }

    public void setAllSpecularSpots(float red, float green, float blue) {
        for (int i = 0; i < LIGHT_COUNT; i++) {
            setSpecularSpot(i, red, green, blue);
        }
    }


    public Vec3 getSpecularPoint(int i) {
        return specularPoint[i];
    }

    public Vec3 getDefaultSpecularPoint() {
        return DEFAULT_SPECULAR;
    }

    public Vec3 getSpecularSpot(int i) {
        return specularSpot[i];
    }

    // ------------ Emission ------------ \\

    public void setEmission(float red, float green, float blue) {
        emission.x = red;
        emission.y = green;
        emission.z = blue;
    }

    public void setEmission(Vec3 rgb) {
        setEmission(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getEmission() {
        return new Vec3(emission);
    }

    // ------------ Shininess ------------ \\

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getShininess() {
        return shininess;
    }

}