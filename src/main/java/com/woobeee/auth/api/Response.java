package com.woobeee.auth.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Api 반환 폼
 * Api 반환 예시 : ErrorResponse
 * {
 *     header : {
 *         isSuccessful : true,
 *         resultCode : 201
 *     } ,
 *     body : {
 *         String title : 중복ID...,
 *         int status : 400,
 *         LocalDateTime : 2024-08-20T15:45:30.123456789,
 *     }
 * }
 *
 * @param <T>
 * @author 김병우
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Response<T> {

    private Header header;

    private Body<T> body;

    public Response(Header header, Body<T> body) {
        this.header = header;
        this.body = body;
    }

    public Response(Header header) {
        this.header = header;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        private boolean isSuccessful;
        private int resultCode;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Body<T> {
        private T data;

        public Body(T data) {
            this.data = data;
        }
    }


    public static <T> Response<T> success(T data) {
        return new Response<>(
                new Header(true, HttpStatus.OK.value()),
                new Body<>(data)
        );
    }
    public static <T> Response<T> success() {
        return new Response<>(
                new Header(true, HttpStatus.OK.value())
        );
    }

    public static <T> Response<T> deleteSuccess() {
        return new Response<>(
                new Header(true, HttpStatus.NO_CONTENT.value())
        );
    }

    public static <T> Response<T> createSuccess() {
        return new Response<>(
                new Header(true, HttpStatus.CREATED.value())
        );
    }

    public static <T> Response<T> createSuccess(T data) {
        return new Response<>(
                new Header(true, HttpStatus.CREATED.value()),
                new Body<>(data)
        );
    }


    public static Response<ErrorResponse> fail(int errorCode, ErrorResponse errorResponseForm) {
        return new Response<>(
                new Header(false, errorCode),
                new Body<>(
                        ErrorResponse.builder()
                                .title(errorResponseForm.title())
                                .status(errorResponseForm.status())
                                .timestamp(errorResponseForm.timestamp())
                                .build()
                )
        );
    }
}
