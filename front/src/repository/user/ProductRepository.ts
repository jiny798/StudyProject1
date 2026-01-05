import HttpRepository from '@/repository/user/HttpRepository.ts'
import type Login from '@/entity/user/account/Login.ts'
import { inject, singleton } from 'tsyringe'
import type ProductWrite from '@/entity/product/ProductWrite.ts'
import Product from '@/entity/product/Product.ts'
import Paging from '@/entity/data/Paging.ts'

@singleton()
export default class ProductRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public get(productId: number) {
    return this.httpRepository.get<Product>({ path: `/api/products/${productId}` }, Product)
  }


  public getListByCategory(page: number, categoryId: number) {
    return this.httpRepository.getList<Product>(
      {
        path: `/api/products?page=${page}&size=10&categoryId=${categoryId}`,
      },
      Product,
    )
  }

}
