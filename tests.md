This list is non-exhaustive, but it's probably a good starting point to improve and stabilize the Android Mobility Collector app.

## Unit tests 

#### Registration tests
- Invalid email should be caught by regexp validation
- Null and empty email addresses are not allowed
- Missing password should be caught 
- Missmatch between password and confirmation of password should be caught 
- Null and empty passwords are not allowed

#### Login tests
- Invalid email should be caught by regexp validation
- Null and empty email addresses are not allowed
- Missing password should be caught 
- Null and empty passwords are not allowed

#### Background service
- Check that service restarts if forcefully killed
- Check that service can be stopped gracefully 
- Accelerometer movement above threshold should trigger GPS sampling 
- (extra resources) [https://medium.com/@josiassena/android-how-to-unit-test-a-service-67e5340544a5] [https://developer.android.com/training/testing/integration-testing/service-testing.html] [http://www.vogella.com/tutorials/AndroidTesting/article.html]

#### Sampling
- If equitime sampling is enabled, the frequency should not change when new GPS points are received 
- Equidistance sampling collection frequency should not be updated on an insufficient location history
- Equidistance sampling collection frequency should be updated on a sufficient location history

#### Database insertion
- Should be able to insert simple locations 
- Should be able to insert locations fused with accelerometer readings 