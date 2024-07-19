package com.jetawy.data.repositories



class AuthRepositoryImpl{
//    private val remoteDataSource: ApiService, private val localDataSource: AuthSharedPreference
//) : AuthRepository {
    /* override suspend fun login(loginData: LoginRequest) = flow {
         emit(RequestStatus.Waiting)
         try {
             val response: Response<LoginResponse> = remoteDataSource.login(loginData)
             if (response.isSuccessful) {
                 emit(RequestStatus.Success(response.body()!!))
                 //we need to add credentials to sp
                 localDataSource.setToken(response.body()!!.accessToken.toString())
                 localDataSource.setUserData(response.body()!!)
             } else {
                 //failed to login
                 emit(RequestStatus.Error(response.message()))
             }
         } catch (e: Exception) {
             emit(RequestStatus.Error(e.message ?: "An error occurred"))
         }
     }
 */
  /*  override fun logout() {
        localDataSource.logout()
    }
*/
  /*  override fun isUserLoggedIn() = localDataSource.isLogged()

    private fun toRequestBody(item: String): RequestBody {
        return item.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }*/
}