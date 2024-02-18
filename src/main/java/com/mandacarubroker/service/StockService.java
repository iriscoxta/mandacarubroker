package com.mandacarubroker.service;

import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;

import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StockService {

    /**
     * O repositório responsável pelo acesso e manipulação dos objetos de ações.
     */
    private final StockRepository stockRepository;

    /**
     * Constrói uma nova instância do {@link StockService}.
     *
     * @param stockRepository O repositório para entidades de estoque. Não deve ser nulo.
     */
    public StockService(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Recupera uma lista de todos os estoques disponíveis.
     *
     * Este método delega a recuperação das entidades de estoque para o respectivo
     * {@link StockRepository} invocando seu método {@code findAll}. O valor retornado
     * lista representa todas as ações presentes no armazenamento de dados subjacente.
     *
     * @return A lista contendo todas as ações disponíveis.
     */
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    /**
     * Recupera uma ação pelo seu identificador único.
     *
     * Este método delega a recuperação de uma entidade de ação específica ao associado
     * {@link StockRepository} invocando seu método {@code findById} com o ID fornecido
     *
     * @param id O identificador único da ação a ser recuperada.
     * @return An {@link Optional} contendo a ação com o ID especificado, se encontrada,
     *         ou um {@link Optional} vazio se a ação não for encontrada.
     */
    public Optional<Stock> getStockById(final String id) {
        return stockRepository.findById(id);
    }

    /**
     * Cria uma nova ação com base nos dados fornecidos.
     *
     * Este método instancia um novo objeto {@link Stock} usando
     * os dados fornecidos pelo {@link RequestStockDTO} fornecido.
     * Em seguida, valida os dados usando o método {@code validateRequestStockDTO}
     * e persiste a nova entidade de estoque no {@link StockRepository}
     * associado usando o método {@code save}.
     *
     * @param data Os dados que representam o novo estoque a ser criado.
     * @return A entidade de estoque criada.
     * @throws ConstraintViolationException Se os dados fornecidos não forem válidos.
     */
    public Stock createStock(final RequestStockDTO data) {
        Stock newStock = new Stock(data);
        validateRequestStockDTO(data);
        return stockRepository.save(newStock);
    }

    /**
     * Atualiza uma ação existente com os dados fornecidos.
     *
     * Este método recupera a entidade de ação existente do repositório de ações associado
     * {@link StockRepository} usando o ID fornecido. Se a ação for encontrada,
     * atualiza seus atributos com os dados fornecidos pelo objeto {@link Stock} fornecido.
     * Persiste a entidade de estoque atualizada de volta ao repositório.
     * usando o método {@code save}.
     *
     * @param id O identificador único da ação a ser atualizada.
     * @param updatedStock Os dados representando a ação atualizada.
     * @return Um {@link Optional} contendo a entidade de ação atualizada, se encontrada,
     *         ou um {@link Optional} vazio se a ação com o ID especificado não for encontrada.
     */
    public Optional<Stock> updateStock(final String id, final Stock updatedStock) {
        return stockRepository.findById(id)
                .map(stock -> {
                    stock.setSymbol(updatedStock.getSymbol());
                    stock.setCompanyName(updatedStock.getCompanyName());
                    stock.setPrice(updatedStock.getPrice());

                    return stockRepository.save(stock);
                });
    }

    /**
     * Exclui uma ação pelo seu identificador único.
     *
     * Este método remove a entidade de ação associada ao
     * ID especificado do armazenamento de dados subjacente,
     * invocando o método {@code deleteById} do
     * {@link StockRepository} associado.
     *
     * @param id O identificador único da ação a ser excluída.
     */
    public void deleteStock(final String id) {
        stockRepository.deleteById(id);
    }

    /**
     * Valida um objeto RequestStockDTO usando a validação de beans.
     *
     * Este método estático utiliza a API de Validação de Beans para validar o objeto {@link RequestStockDTO} fornecido.
     * Ele verifica as restrições especificadas por meio de anotações nos campos do DTO. Se a validação falhar,
     * uma {@link ConstraintViolationException} é lançada, fornecendo detalhes sobre os erros de validação.
     *
     * @param data O objeto RequestStockDTO a ser validado.
     * @throws ConstraintViolationException Se a validação do RequestStockDTO falhar,
     * contendo detalhes dos erros de validação.
     */
    public static void validateRequestStockDTO(final RequestStockDTO data) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<RequestStockDTO>> violations = validator.validate(data);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed. Details: ");

            for (ConstraintViolation<RequestStockDTO> violation : violations) {
                errorMessage.append(
                        String.format("[%s: %s], ",
                                violation.getPropertyPath(),
                                violation.getMessage()
                        )
                );
            }

            errorMessage.delete(errorMessage.length() - 2, errorMessage.length());

            throw new ConstraintViolationException(errorMessage.toString(), violations);
        }
    }

    /**
     * Valida o RequestStockDTO e cria uma nova ação se a validação for bem-sucedida.
     *
     * Este método primeiro valida o {@link RequestStockDTO} fornecido usando o método {@code validateRequestStockDTO}.
     * Se a validação for bem-sucedida, um novo objeto {@link Stock} é instanciado usando os dados fornecidos e,
     * em seguida, é persistido no {@link StockRepository} associado usando o método {@code save}.
     *
     * @param data O objeto RequestStockDTO contendo os dados para criar uma nova ação.
     * @throws ConstraintViolationException Se a validação do RequestStockDTO falhar, contendo detalhes dos erros de validação.
     */
    public void validateAndCreateStock(final RequestStockDTO data) {
        validateRequestStockDTO(data);

        Stock newStock = new Stock(data);
        stockRepository.save(newStock);
    }
}