package org.aryan.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.aryan.Entity.Album;
import org.aryan.Exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ElasticsearchService {

    @Inject
    ElasticsearchClient client;

    public String index(Album album) throws BusinessException {
        try{
            IndexRequest<Album> request = IndexRequest.of(
                    b -> b.index("albums")
                            .document(album));
            IndexResponse response = client.index(request);
            return response.id();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BusinessException("The index operation encountered errors.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    public List<Album> search(String match) throws BusinessException {
        try{
            SearchRequest searchRequest = SearchRequest.of(
                    b -> b.index("albums")
                            .query(q -> q.bool(
                                    BoolQuery.of(bq -> bq
                                            .should(
                                                    MultiMatchQuery.of(m -> m
                                                            .query(match)
                                                            .fields("name^2", "artists.name")
                                                    )._toQuery()
                                            )
                                    )
                            ))
            );
            SearchResponse<Album> searchResponse = client.search(searchRequest, Album.class);
            System.out.println(searchResponse.toString());
            HitsMetadata<Album> hits = searchResponse.hits();
            System.out.println(hits.total());
            return hits.hits().stream().map(hit -> hit.source()).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BusinessException("The search operation encountered errors.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }
}

