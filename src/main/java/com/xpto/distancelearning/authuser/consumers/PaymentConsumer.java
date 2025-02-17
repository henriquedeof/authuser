package com.xpto.distancelearning.authuser.consumers;

import com.xpto.distancelearning.authuser.dtos.PaymentEventDto;
import com.xpto.distancelearning.authuser.enums.PaymentControl;
import com.xpto.distancelearning.authuser.enums.RoleType;
import com.xpto.distancelearning.authuser.enums.UserType;
import com.xpto.distancelearning.authuser.models.UserModel;
import com.xpto.distancelearning.authuser.services.RoleService;
import com.xpto.distancelearning.authuser.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${dl.broker.queue.paymentEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${dl.broker.exchange.paymentEvent}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
    )
    public void listenPaymentEvent(@Payload PaymentEventDto paymentEventDto){
        UserModel userModel = userService.findById(paymentEventDto.getUserId()).get();
        var roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is Not Found."));

        switch (PaymentControl.valueOf(paymentEventDto.getPaymentControl())){
            case EFFECTED:
                if (userModel.getUserType().equals(UserType.USER)) {
                    userModel.setUserType(UserType.STUDENT);
                    userModel.getRoles().add(roleModel);
                    userService.updateUser(userModel);
                }
                break;
            case REFUSED:
                if (userModel.getUserType().equals(UserType.STUDENT)) {
                    userModel.setUserType(UserType.USER);
                    userModel.getRoles().remove(roleModel);
                    userService.updateUser(userModel);
                }
                break;
            case ERROR:
                System.out.println("Payment with ERROR");
        }
    }
}