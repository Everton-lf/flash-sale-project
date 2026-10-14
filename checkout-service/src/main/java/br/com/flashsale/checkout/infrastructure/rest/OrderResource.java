package br.com.flashsale.checkout.infrastructure.rest;

import br.com.flashsale.checkout.application.dto.CreateOrderRequest;
import br.com.flashsale.checkout.application.dto.ErrorResponse;
import br.com.flashsale.checkout.application.dto.OrderResponse;
import br.com.flashsale.checkout.application.service.OrderCheckoutService;
import br.com.flashsale.checkout.domain.exceptions.InsufficientStockException;
import br.com.flashsale.checkout.domain.exceptions.OrderNotFoundException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/v1/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderCheckoutService checkoutService;

    @POST
    public Response createOrder(@Valid CreateOrderRequest request) {
        try {
            OrderResponse response = checkoutService.createOrder(request);
            return Response.status(Response.Status.ACCEPTED).entity(response).build();
        } catch (InsufficientStockException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{pedido_id}")
    public Response getOrder(@PathParam("pedido_id") UUID pedidoId) {
        try {
            OrderResponse response = checkoutService.getOrderById(pedidoId);
            return Response.ok(response).build();
        } catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
