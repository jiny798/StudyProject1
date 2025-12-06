import HttpRepository from '@/repository/HttpRepository'
import type Login from '@/entity/user/Login'
import { inject, singleton } from 'tsyringe'
import UserProfile from '@/entity/user/UserProfile'
import Product from "@/entity/product/Product.ts";

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

  public getList(page: number) {
    return this.httpRepository.getList<UserProfile>(
      {
        path: `/api/admin/users?page=${page}&size=10`,
      },
      UserProfile,
    )
  }
}
