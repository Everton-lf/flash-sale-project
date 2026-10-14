package br.com.flashsale.checkout.infrastructure.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RedisStockService {

    private static final Logger LOG = Logger.getLogger(RedisStockService.class);

    @Inject
    RedisDataSource redisDataSource;

    /**
     * Decrementa a quantidade diretamente no Redis de forma atômica (String/String).
     */
    public boolean reserveStock(String productId, int quantity) {
        String key = "stock:" + productId;

        try {
            // Usa String.class para garantir compatibilidade nativa com redis-cli SET/GET
            ValueCommands<String, String> valueCommands = redisDataSource.value(String.class);

            // DECRBY nativo do Redis
            long saldoRestante = valueCommands.decrby(key, (long) quantity);

            LOG.infof("RESERVA REDIS - Chave: %s | Solicitado: %d | Saldo Retornado: %d", key, quantity, saldoRestante);

            if (saldoRestante < 0) {
                // Se ficou negativo, estorna o decremento e recusa o pedido
                valueCommands.incrby(key, (long) quantity);
                LOG.warnf("Estoque insuficiente para %s. Revertendo decremento...", key);
                return false;
            }

            return true;

        } catch (Exception e) {
            LOG.errorf("ERRO NO REDIS ao reservar estoque para %s: %s", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Estorna a quantidade no Redis.
     */
    public void incrementStock(String productId, int quantity) {
        String key = "stock:" + productId;
        try {
            ValueCommands<String, String> valueCommands = redisDataSource.value(String.class);
            long novoSaldo = valueCommands.incrby(key, (long) quantity);
            LOG.infof("SAGA COMPENSAÇÃO - Estoque estornado no Redis para %s. Novo saldo: %d", key, novoSaldo);
        } catch (Exception e) {
            LOG.errorf("Erro ao estornar estoque no Redis para %s: %s", key, e.getMessage());
        }
    }
}