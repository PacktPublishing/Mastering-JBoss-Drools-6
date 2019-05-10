[keyword]use eshop model=import org.drools.devguide.eshop.model.Customer;import org.drools.devguide.eshop.model.Order;
[keyword]avoid looping=no-loop true
[when]There is a Customer=$c:Customer()
[when]- without a Category set=category == Customer.Category.NA
[when]The Customer has {operator:[\w\s]+\d+} Orders=Number(intValue {operator}) from accumulate (Order(customer == $c), sum(1))
[when]more than {n:\d*}=> {n}
[when]less than {n:\d*}=< {n}
[when]between {n1:\d*} and {n2:\d*}= >= {n1} && <= {n2}
[then]Update Customer=modify($c)\{\}
[then]- set Category to {category:\w*}=setCategory(Customer.Category.{category})