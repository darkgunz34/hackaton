package fr.poc.hackaton.business.dto;

import lombok.Data;

@Data
public class ExtractionDto {
    String userId;
    String transactionId;
    String bankAccountId;
    String financialInstitutionId;
    String datePosted;
    String trntype;
    String bankAccountType;
    String coalesce;
    String balance;
    String originalLabel;
    String thirdParty;
    String category;
    String amount;
    String country;
    String postcode;
    String city;
    String insightsIdentifier;
    String insightsMatched;
    String insightsCategoryName;
    String insightsCategoryId;
    String insightsCategoryLogoUrl;
    String insightsThirdPartyName;
    String insightsMerchantLogoUrl;
    String insightsMerchantLocationCity;
    String insightsMerchantLocationCountry;
    String insightsMerchantLocationPostalCode;
    String insightsMerchantLocationAddress;
    String insightsMerchantLocationGpxLongitude;
    String insightsMerchantLocationGpxLatitude;
}
