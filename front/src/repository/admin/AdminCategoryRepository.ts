import {inject, singleton} from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import Category from '@/entity/product/Category.ts'
import type CategoryWrite from "@/entity/product/CategoryWrite.ts";

@singleton()
export default class AdminCategoryRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public write(request: CategoryWrite) {
    return this.httpRepository.post({
      path: '/api/admin/categories',
      body: request,
    })
  }

  public modify(id: number, request: CategoryWrite) {
    return this.httpRepository.patch({
        path: `/api/admin/categories/${id}`,
        body: request,
      }
    )
  }

  public getAll() {
    return this.httpRepository.getAll<Category>(
      {
        path: `/api/admin/categories`,
      },
      Category,
    )
  }

  public getLeafCategories() {
    return this.httpRepository.getAll<Category>(
      {
        path: `/api/admin/categories/leaves`,
      },
      Category,
    )
  }

  public get(id: number) {
    return this.httpRepository.get<Category>(
      {
        path: `/api/admin/categories/${id}`,
      },
      Category,
    )
  }

  public delete(id: number) {
    return this.httpRepository.delete({
      path: `/api/admin/categories/${id}`,
    })
  }
}
