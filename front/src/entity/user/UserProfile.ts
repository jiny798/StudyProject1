import {Transform} from "class-transformer";
import {DateTimeFormatter, LocalDateTime} from "@js-joda/core";

export default class UserProfile {
  public id = 0
  public name = ''
  public email = ''

  // @Transform(({ value }) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  public createdAt = LocalDateTime.now()
}
