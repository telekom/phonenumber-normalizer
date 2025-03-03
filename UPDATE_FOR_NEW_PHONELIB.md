# How to adapt to a new Version of PhoneLib

If Google updates its [LibPhoneNumber](https://github.com/google/libphonenumber), this project should be updated to use that new version. This file is a step by step instruction how to do this:

1. Create a new local branch - best name it ```phonelib/X_YY_ZZ``` so it is easily seen that this branch is just an update for the version X.YY.ZZ, without any new features.

2. Update [pom.xml](pom.xml) to use the new Google's LibPhoneNumber version:
   ```
   <dependency>
     <groupId>com.googlecode.libphonenumber</groupId>
     <artifactId>libphonenumber</artifactId>
     <version>X.YY.ZZ</version>
   </dependency>
   ```
   
3. Check on Maven Central ```https://central.sonatype.com/artifact/com.googlecode.libphonenumber/libphonenumber/X.YY.ZZ/dependents``` the version number for ```geocoder``` (referred as A.BBB).

4. Update [pom.xml](pom.xml) to use the new geocoder version in testing:
   ```
        <dependency>
            <groupId>com.googlecode.libphonenumber</groupId>
            <artifactId>geocoder</artifactId>
            <version>A.BBB</version>
            <scope>test</scope>
        </dependency>
   ```
   
5. Run all unit test and check log messages if Google's LibPhoneNumber still is not correctly:
   a) normalizing specific number -> this project is still necessary
   b) labeling specific numbers -> own area labels for DE still necessary
   if there are corrections or additional mismatches listed - name those in the commit message and update tests.

6. Remove ```-SNAPSHOT``` from version tag in [pom.xml](pom.xml)

7. Commit & Push the Snapshot with a message like:
   ```
   Use Google's LibPhoneNumber X.YY.ZZ and prepare release
   ```
   
8. Go to Github and create a pull request for the branch.

9. Wait until pull request passed all checks - merge or ask a maintainer to merge the pull request into main.

10. After merge has finished, draft a new Release. Use as tag the ```v```+ the version number of the pom, where you removed ```-SNAPSHOT```. As Release title use  ```Google's LibPhoneNumber X.YY.ZZ``` and add a message like:
    ```
    Use Google's latest LibPhoneNumber version from four days ago.
    ```
    Keep the flag "Set as the latest release" and press Publish release.

11. Wait until the publishing action is finished (the project is automatically released to [Maven Central](https://central.sonatype.com/artifact/de.telekom.phonenumber/normalizer/))

12. Add ```-SNAPSHOT``` to version tag and increase its last number in [pom.xml](pom.xml)

13. Commit & Push the Snapshot with a message like:
    ```
    Start Snapshot NEW_VERSION after release OLD_VERSION
    ```

14. Go to Github and create a pull request for the branch.

15. Wait until pull request passed all checks - merge or ask a maintainer to merge the pull request into main.

16. Delete the branch ```phonelib/X_YY_ZZ```.

Congratulation! You have updated the project to Google'S [current LibPhoneNumber version](https://central.sonatype.com/artifact/com.googlecode.libphonenumber/libphonenumber).
