import AxiosHttpClient, { type HttpRequestConfig } from '@/http/AxiosHttpClient.ts'
import { inject, singleton } from 'tsyringe'
import { plainToInstance } from 'class-transformer'
import Paging from '@/entity/data/Paging.ts'

@singleton()
export default class HttpRepository {
  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {}

  public async get<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T }): Promise<T> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => plainToInstance(clazz, response.message))
  }

  public async getList<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T }): Promise<Paging<T>> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      console.log("앙 응답 : " + response)
      const paging = plainToInstance<Paging<T>, any>(Paging, response.message)
      // const items: T[] = plainToInstance<T, any>(clazz, response.items)
      const items: T[] = plainToInstance<T, any>(clazz, response.message.items ?? []) as T[]
      paging.setItems(items)
      return paging
    })
  }

  public async getAll<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T }): Promise<T[]> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      const data = response?.message ?? []
      return plainToInstance<T, any>(clazz, data) as T[] // data 를 받아 T 타입으로
    })
  }

  public async post<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T } | null = null): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'POST' })
      .then((response) => (clazz ? plainToInstance(clazz, response) : (response as T)))
    // .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }

  public async patch<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T } | null = null): Promise<T> {
    return this.httpClient
      .request({
        ...config,
        method: 'PATCH',
      })
      .then((response) => (clazz ? plainToInstance(clazz, response) : (response as T)))
    // .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }

  public async put<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T } | null = null): Promise<T> {
    return this.httpClient
      .request({
        ...config,
        method: 'PUT',
      })
      .then((response) => (clazz ? plainToInstance(clazz, response) : (response as T)))
    // .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }

  public async delete<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T } | null = null): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'DELETE' })
      .then((response) => (clazz ? plainToInstance(clazz, response) : (response as T)))
    // .then((response) => plainToInstance(clazz !== null ? clazz : Null, response))
  }
}
