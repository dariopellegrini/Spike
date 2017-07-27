package com.dariopellegrini.spike.spike.response

/**
 * Created by dariopellegrini on 25/07/17.
 */
enum class SpikeError {
    noConnection,
    jsonParsingError,
    unknownError,
    badRequest, // 400
    unauthorized, // 401
    paymentRequired, // 402
    forbidden, // 403
    notFound, // 404
    methodNotAllowed, // 405
    notAcceptable, // 406
    proxyAuthenticationRequired, // 407
    requestTimeout, // 408
    conflict, // 409
    gone, // 410
    lengthRequired, // 411
    preconditionFailed, // 412
    requestEntityTooLarge, // 413
    requestURITooLong, // 414
    unsupportedMediaType, // 415
    requestedRangeNotSatisfiable, // 416
    expectationFailed, // 417
    enhanceYourCalm, // 418
    misdirectedRequest, // 421
    unprocessableEntity, // 422
    locked, // 423
    failedDependency, // 424
    upgradeRequired , // 426
    preconditionRequired, // 428
    tooManyRequests, // 429
    requestHeaderFieldsTooLarge, // 431
    unavailableForLegalReasons, // 451
    internalServerError, // 500
    notImplemented, // 501
    badGateway, // 502
    serviceUnavailable, // 503
    gatewayTimeout, // 504
    HTTPVersionNotSupported, // 505
    bandwidthLimitExceeded, // 509
}