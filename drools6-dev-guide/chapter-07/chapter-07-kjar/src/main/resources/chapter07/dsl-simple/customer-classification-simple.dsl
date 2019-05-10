# Simple DSL example file

[keyword]avoid looping=no-loop true
[when]There is a Customer=$c:Customer()
[when]- with age between {low:\d*} and {high:\d*}=age >= {low}, age <= {high}
[when]- who is older than {low:\d*}=age > {low}
[when]- without a Category set=category == Customer.Category.NA
[then]Set Customer Category to {category:\w*}=modify($c)\{setCategory(Customer.Category.{category})\};