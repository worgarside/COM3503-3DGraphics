import gmaths.*;

/**
 * This class stores the Material properties for a Mesh
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 */

public class Material {

    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
    public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.5f, 0.5f, 0.5f);
    public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);
    public static final float DEFAULT_SHININESS = 32;

    private Vec3 ambient;
    private Vec3 diffusePoint;
    private Vec3 diffuseSpot;
    private Vec3 specularPoint;
    private Vec3 specularSpot;
    private Vec3 emission;
    private float shininess;

    public Material() {
        ambient = new Vec3(DEFAULT_AMBIENT);
        diffusePoint = new Vec3(DEFAULT_DIFFUSE);
        diffuseSpot = new Vec3(DEFAULT_DIFFUSE);
        specularPoint = new Vec3(DEFAULT_SPECULAR);
        specularSpot = new Vec3(DEFAULT_SPECULAR);
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

    public void setDiffusePoint(float red, float green, float blue) {
        diffusePoint.x = red;
        diffusePoint.y = green;
        diffusePoint.z = blue;
    }

    public void setDiffuseSpot(float red, float green, float blue) {
        diffuseSpot.x = red;
        diffuseSpot.y = green;
        diffuseSpot.z = blue;
    }

//    public void setDiffuse(Vec3 rgb) {
//        setDiffuse(rgb.x, rgb.y, rgb.z);
//    }

    public Vec3 getDiffusePoint() {
        return new Vec3(diffusePoint);
    }

    public Vec3 getDiffuseSpot() {
        return new Vec3(diffuseSpot);
    }

    // ------------ Specular ------------ \\

    public void setSpecularPoint(float red, float green, float blue) {
        specularPoint.x = red;
        specularPoint.y = green;
        specularPoint.z = blue;
    }

    public void setSpecularSpot(float red, float green, float blue) {
        specularSpot.x = red;
        specularSpot.y = green;
        specularSpot.z = blue;
    }

//    public void setSpecular(Vec3 rgb) {
//        setSpecular(rgb.x, rgb.y, rgb.z);
//    }

    public Vec3 getSpecularPoint() {
        return new Vec3(specularPoint);
    }
    public Vec3 getSpecularSpot() {
        return new Vec3(specularSpot);
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