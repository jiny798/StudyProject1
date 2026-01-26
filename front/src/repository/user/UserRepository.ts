import HttpRepository from '@/repository/user/HttpRepository.ts'
import type Login from '@/entity/user/account/Login.ts'
import { inject, singleton } from 'tsyringe'
import UserProfile from '@/entity/user/account/UserProfile.ts'
import Product from "@/entity/product/Product.ts";
import type UpdatePassword from "@/entity/user/account/PasswordUpdate.ts";
import PasswordUpdate from "@/entity/user/account/PasswordUpdate.ts";

@singleton()
export default class UserRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public login(request: Login) {
    return this.httpRepository.post({
      path: '/api/login',
      body: request,
    })
  }

  public signup(request: Login) {
    return this.httpRepository.post({
      path: '/api/users',
      body: request,
    })
  }

  public getProfile() {
    return this.httpRepository.get<UserProfile>(
      {
        path: '/api/users/me',
      },
      UserProfile,
    )
  }

  public updatePassword(request: PasswordUpdate) {
    return this.httpRepository.put({
      path: '/api/users/password',
      body: request,
    })
  }


  public getList(page: number) {
    return this.httpRepository.getList<UserProfile>(
      {
        path: `/api/admin/users?page=${page}&size=10`,
      },
      UserProfile,
    )
  }
}
