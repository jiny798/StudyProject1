import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Transform } from 'class-transformer'

export default class Product {
  public id: number | null = null
  public name = ''
  public price: number | null = null
  public categoryId: number | null = null
  public description = ''
  public productImages: string[] = []
  public options: Record<string, any> = {}

  @Transform(({ value }) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  public regDate = LocalDateTime.now()

  public getDisplayRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern('yyyy년 MM월 dd일 HH시'))
  }

  public getDisplaySimpleRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern('yyyy-MM-dd'))
  }
}
