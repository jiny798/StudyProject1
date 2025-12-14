import HttpRepository from '@/repository/user/HttpRepository.ts'
import { inject, singleton } from 'tsyringe'
import type PostWrite from '@/entity/post/PostWrite.ts'
import Post from '@/entity/post/Post.ts'

@singleton()
export default class PostRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public write(request: PostWrite) {
    return this.httpRepository.post({
      path: '/api/posts',
      body: request,
    })
  }

  public get(postId: number) {
    return this.httpRepository.get<Post>({ path: `/api/posts/${postId}` }, Post)
  }

  public getList(page: number) {
    return this.httpRepository.getList<Post>(
      {
        path: `/api/posts?page=${page}&size=10`,
      },
      Post,
    )
  }

  public delete(postId: number) {
    return this.httpRepository.delete({
      path: `/api/posts/${postId}`,
    })
  }
}
