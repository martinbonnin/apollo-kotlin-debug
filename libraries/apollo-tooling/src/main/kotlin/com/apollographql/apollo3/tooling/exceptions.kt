package com.apollographql.apollo3.tooling

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloHttpException

/**
 * returns an [Exception] that represents [ApolloResponse]
 *
 * Handles:
 * - the case where [ApolloResponse.exception] is null and [ApolloResponse.data] is also null
 * - reading the HTTP body if [com.apollographql.apollo3.ApolloClient.Builder.httpExposeErrorBody] is true
 */
internal fun <D: Operation.Data> ApolloResponse<D>.toException(context: String): Exception {
  return when (val e = exception) {
    is ApolloHttpException -> {
      val body = e.body?.use { it.readUtf8() } ?: ""
      Exception("$context: (code: ${e.statusCode})\n$body", e)
    }

    null -> {
      Exception("$context: ${errors?.joinToString { it.message }}")
    }

    else -> {
      Exception("$context: ${e.message}", e)
    }
  }
}