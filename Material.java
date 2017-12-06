import gmaths.*;

/**
 * RobotHand.java
 * Material object to hold properties of the material of a scene object when illuminated
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Material {

    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
    public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.05f, 0.05f, 0.05f);
    public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);
    public static final float DEFAULT_SHININESS = 32;

    private Vec3 ambient;
    private Vec3[] diffusePoint = new Vec3[Arty.lightCount];
    private Vec3[] diffuseSpot = new Vec3[Arty.lightCount];
    private Vec3[] specularPoint = new Vec3[Arty.lightCount];
    private Vec3[] specularSpot = new Vec3[Arty.lightCount];
    private Vec3 emission;
    private float shininess;

    /**
     * Constructor for the Material object
     * Sets all light properties to the defaults (some are overwritten later)
     */
    public Material() {
        ambient = new Vec3(DEFAULT_AMBIENT);
        for (int i = 0; i < Arty.lightCount; i++) {
            diffusePoint[i] = new Vec3(DEFAULT_DIFFUSE);
            diffuseSpot[i] = new Vec3(DEFAULT_DIFFUSE);
            specularPoint[i] = new Vec3(DEFAULT_SPECULAR);
            specularSpot[i] = new Vec3(DEFAULT_SPECULAR);
        }

        emission = new Vec3(DEFAULT_EMISSION);
        shininess = DEFAULT_SHININESS;
    }

    // ------------ Setters ------------ \\

    /**
     * Sets the Ambient value of the Material
     *
     * @param red - red value of the Ambient
     * @param green - green value of the Ambient
     * @param blue - blue value of the Ambient
     */
    public void setAmbient(float red, float green, float blue) {
        ambient.x = red;
        ambient.y = green;
        ambient.z = blue;
    }

    /**
     * Sets the Ambient value of the Material
     *
     * @param rgb - the RGB vlaue of the Ambient as a Vec3
     */
    public void setAmbient(Vec3 rgb) {
        setAmbient(rgb.x, rgb.y, rgb.z);
    }

    /**
     * Sets the Diffuse from a specific point light
     *
     * @param i - lightSource reference number
     * @param red - red value of the Diffuse
     * @param green - green value of the Diffuse
     * @param blue - blue value of the Diffuse
     */
    public void setDiffusePoint(int i, float red, float green, float blue) {
        diffusePoint[i].x = red;
        diffusePoint[i].y = green;
        diffusePoint[i].z = blue;
    }

    /**
     * Sets the Diffuse from a specific spotlight
     *
     * @param i - lightSource reference number
     * @param red - red value of the Diffuse
     * @param green - green value of the Diffuse
     * @param blue - blue value of the Diffuse
     */
    public void setDiffuseSpot(int i, float red, float green, float blue) {
        diffuseSpot[i].x = red;
        diffuseSpot[i].y = green;
        diffuseSpot[i].z = blue;
    }

    /**
     * Sets Diffuse from all point lights
     *
     * @param red - red value of the Diffuse
     * @param green - green value of the Diffuse
     * @param blue - blue value of the Diffuse
     */
    public void setAllDiffusePoints(float red, float green, float blue) {
        for (int i = 0; i < Arty.lightCount; i++) {
            setDiffusePoint(i, red, green, blue);
        }
    }

    /**
     * Sets Diffuse from all spotlights
     *
     * @param red - red value of the Diffuse
     * @param green - green value of the Diffuse
     * @param blue - blue value of the Diffuse
     */
    public void setAllDiffuseSpots(float red, float green, float blue) {
        for (int i = 0; i < Arty.lightCount; i++) {
            setDiffuseSpot(i, red, green, blue);
        }
    }

    /**
     * Sets the Specular from a specific point light
     *
     * @param i - lightSource reference number
     * @param red - red value of the Specular
     * @param green - green value of the Specular
     * @param blue - blue value of the Specular
     */
    public void setSpecularPoint(int i, float red, float green, float blue) {
        specularPoint[i].x = red;
        specularPoint[i].y = green;
        specularPoint[i].z = blue;
    }

    /**
     * Sets the Specular from a specific spotlight
     *
     * @param i - lightSource reference number
     * @param red - red value of the Specular
     * @param green - green value of the Specular
     * @param blue - blue value of the Specular
     */
    public void setSpecularSpot(int i, float red, float green, float blue) {
        specularSpot[i].x = red;
        specularSpot[i].y = green;
        specularSpot[i].z = blue;
    }

    /**
     * Sets Specular from all point lights
     *
     * @param red - red value of the Specular
     * @param green - green value of the Specular
     * @param blue - blue value of the Specular
     */
    public void setAllSpecularPoints(float red, float green, float blue) {
        for (int i = 0; i < Arty.lightCount; i++) {
            setSpecularPoint(i, red, green, blue);
        }
    }

    /**
     * Sets Specular from all spotlights
     *
     * @param red - red value of the Specular
     * @param green - green value of the Specular
     * @param blue - blue value of the Specular
     */
    public void setAllSpecularSpots(float red, float green, float blue) {
        for (int i = 0; i < Arty.lightCount; i++) {
            setSpecularSpot(i, red, green, blue);
        }
    }

    /**
     * Sets the emission value of the Material
     *
     * @param red - red value of the Emission
     * @param green - green value of the Emission
     * @param blue - blue value of the Emission
     */
    public void setEmission(float red, float green, float blue) {
        emission.x = red;
        emission.y = green;
        emission.z = blue;
    }

    /**
     * Sets the emission value of the Material
     *
     * @param rgb - the RGB vlaue of the Emission as a Vec3
     */
    public void setEmission(Vec3 rgb) {
        setEmission(rgb.x, rgb.y, rgb.z);
    }

    /**
     * Sets the shininess of a Material
     *
     * @param shininess - the shininess value as a float
     */
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    // ------------ Getters ------------ \\

    /**
     * Gets the Ambient value of a Material
     *
     * @return the Ambient as a Vec3
     */
    public Vec3 getAmbient() {
        return new Vec3(ambient);
    }

    /**
     * Gets the Diffuse of the Material from a specific point light
     *
     * @param i - lightSource reference number
     * @return - Diffuse value as a Vec3
     */
    public Vec3 getDiffusePoint(int i) {
        return diffusePoint[i];
    }

    /**
     * Gets the Diffuse of the Material from a specific spotlight
     *
     * @param i - lightSource reference number
     * @return - Diffuse value as a Vec3
     */
    public Vec3 getDiffuseSpot(int i) {
        return diffuseSpot[i];
    }

    /**
     * Gets the Specular of the Material from a specific point light
     *
     * @param i - lightSource reference number
     * @return - the Specular value as a Vec3
     */
    public Vec3 getSpecularPoint(int i) {
        return specularPoint[i];
    }

    /**
     * Gets the Specular of the Material from a specific spotlight
     *
     * @param i - lightSource reference number
     * @return - the Specular value as a Vec3
     */
    public Vec3 getSpecularSpot(int i) {
        return specularSpot[i];
    }

    /**
     * Gets Emission value of a material
     *
     * @return - Emission value as Vec3
     */
    public Vec3 getEmission() {
        return new Vec3(emission);
    }

    /**
     * Gets Shininess value of a material
     *
     * @return - Shininess value as Vec3
     */
    public float getShininess() {
        return shininess;
    }

}